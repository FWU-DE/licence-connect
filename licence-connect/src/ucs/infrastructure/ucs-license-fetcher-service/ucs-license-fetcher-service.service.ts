import { Injectable } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { UCSStudentId, UCSStudent } from '@ucs/domain/ucs-types';
import { UCSLicenceSource } from '@ucs/domain/ucs-licences-source';
import { UcsConfigurationService } from '../configuration/ucs-configuration.service';
import { firstValueFrom } from 'rxjs';
import { UCSLicenceProviderConfig } from '../configuration/ucs-licence-provider-config';

const authEndpoint = 'apis/auth/token';
const licenceEndpoint = 'apis/bildungslogin/v1/user';

/**
 * Licence Service providing access to the UCS system of Mecklenburg-Vorpommern
 */
@Injectable()
export class UCSLicenseFetcherService implements UCSLicenceSource {
  constructor(
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

    const response = await firstValueFrom(
      this.httpService.get(ucsLicenceEndpoint, { headers: headers }),
    );

    return response.data as unknown as UCSStudent;
  }
}
