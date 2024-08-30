import { HttpException, Inject, Injectable } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { UCSStudentId, UCSStudent } from '@ucs/domain/ucs-types';
import { UCSLicenceSource } from '@ucs/domain/ucs-licences-source';
import { UcsConfigurationService } from '../configuration/ucs-configuration.service';
import { firstValueFrom } from 'rxjs';
import { UCSLicenceProviderConfig } from '../configuration/ucs-licence-provider-config';
import {
  LCLogger,
  LOGGER_TOKEN,
} from '@cross-cutting-concerns/logging/domain/logger';
import { AxiosError } from 'axios';

const authEndpoint = 'apis/auth/token';
const licenceEndpoint = 'apis/bildungslogin/v1/user';

/**
 * Licence Service providing access to the UCS system of Mecklenburg-Vorpommern
 */
@Injectable()
export class UCSLicenseFetcherService implements UCSLicenceSource {
  constructor(
    @Inject(LOGGER_TOKEN) private readonly logger: LCLogger,
    private readonly ucsConfigurationService: UcsConfigurationService,
    private readonly httpService: HttpService,
  ) {}

  public async getUCSStudentFromId(id: UCSStudentId): Promise<UCSStudent> {
    const studentData = this.fetchLicenseDataFromUCS(id);
    return studentData;
  }

  private async fetchLicenseDataFromUCS(
    studentId: string,
  ): Promise<UCSStudent> {
    const ucsConfig = this.ucsConfigurationService.getUcsConfiguration();

    const bearerToken = await this.fetchAuthToken(ucsConfig);

    const ucsStudent = await this.fetchLicences(
      ucsConfig,
      studentId,
      bearerToken.bearer,
    );

    return ucsStudent;
  }

  private async fetchAuthToken(
    config: UCSLicenceProviderConfig,
  ): Promise<{ bearer: string }> {
    const ucsAuthEndpoint = `${config.url}/${authEndpoint}`;

    const params = new URLSearchParams();
    params.append('username', config.technicalUserName);
    params.append('password', config.technicalUserPassword);

    const response = await firstValueFrom(
      this.httpService.post(ucsAuthEndpoint, params),
    );

    return { bearer: response.data.access_token } as unknown as {
      bearer: string;
    };
  }

  private handleError(
    studentId: string,
    requestUrl: string,
    error: HttpException | AxiosError | any,
  ): never {
    if (error instanceof HttpException) {
      throw new HttpException(
        `Failed to fetch licences for student with id ${studentId} on url ${requestUrl}`,
        error.getStatus(),
        { cause: error },
      );
    } else if (error.response) {
      throw new HttpException(
        `Failed to fetch licences for student with id ${studentId} on url ${requestUrl}`,
        error.response.status,
        { cause: error },
      );
    } else {
      throw new Error(
        `Failed to fetch licences for student with id ${studentId} on url ${requestUrl}. Failed with error ${error}`,
      );
    }
  }

  private async fetchLicences(
    config: UCSLicenceProviderConfig,
    studentId: string,
    bearerToken: string,
  ): Promise<UCSStudent> {
    const ucsLicenceEndpoint = `${config.url}/${licenceEndpoint}/${studentId}`;

    const headers = {
      Authorization: `Bearer ${bearerToken}`,
      'content-type': 'application/x-www-form-urlencoded',
    };

    try {
      const response = await firstValueFrom(
        this.httpService.get(ucsLicenceEndpoint, { headers: headers }),
      );
      return response.data as unknown as UCSStudent;
    } catch (err) {
      return this.handleError(studentId, ucsLicenceEndpoint, err);
    }
  }
}
