import { HttpException, Inject, Injectable } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { UcsStudent } from '@ucs/domain/ucs-types';
import { firstValueFrom } from 'rxjs';
import { UcsStudentProvider } from '../../domain/ucs-student-provider';
import { LCLogger } from '@cross-cutting-concerns/logging/domain/logger';
import { AxiosError } from 'axios';
import { LOGGER_TOKEN } from '@cross-cutting-concerns/logging/infrastructure/logger.module';

const authEndpoint = 'apis/auth/token';
const licenceEndpoint = 'apis/bildungslogin/v1/user';

/**
 * Licence Service providing access to the UCS system of Mecklenburg-Vorpommern
 */
@Injectable()
export class UcsLicenseFetcherService {
  constructor(
    @Inject(LOGGER_TOKEN) private readonly logger: LCLogger,
    private readonly httpService: HttpService,
  ) {}

  public async fetchUcsStudentFromId(
    studentId: string,
    ucsStudentProvider: UcsStudentProvider,
  ): Promise<UcsStudent> {
    const bearerToken = await this.fetchAuthToken(ucsStudentProvider);

    const ucsStudent = await this.fetchStudents(
      ucsStudentProvider,
      studentId,
      bearerToken.bearer,
    );

    return ucsStudent;
  }

  private async fetchAuthToken(
    config: UcsStudentProvider,
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

  private async fetchStudents(
    config: UcsStudentProvider,
    studentId: string,
    bearerToken: string,
  ): Promise<UcsStudent> {
    const ucsLicenceEndpoint = `${config.url}/${licenceEndpoint}/${studentId}`;

    const headers = {
      Authorization: `Bearer ${bearerToken}`,
      'content-type': 'application/x-www-form-urlencoded',
    };

    try {
      const response = await firstValueFrom(
        this.httpService.get(ucsLicenceEndpoint, { headers: headers }),
      );
      return response.data as unknown as UcsStudent;
    } catch (err) {
      return this.handleError(studentId, ucsLicenceEndpoint, err);
    }
  }
}
