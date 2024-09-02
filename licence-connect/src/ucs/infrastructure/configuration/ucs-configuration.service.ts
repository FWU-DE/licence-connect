import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { UcsStudentProvider } from '../../domain/ucs-student-provider';

@Injectable()
export class UcsConfigurationService {
  constructor(private readonly nestConfigurationService: ConfigService) {}

  public getApiKey(): string | undefined {
    return this.nestConfigurationService.get<string>('UCS_API_KEY');
  }

  public getUcsConfiguration(): UcsStudentProvider {
    const url = this.getRequiredConfigParameter('UCS_URL');
    const technicalUserName = this.getRequiredConfigParameter(
      'UCS_TECHNICAL_USER_NAME',
      'No TechnicalUser provided for the UCS System system',
    );
    const technicalUserPassword = this.getRequiredConfigParameter(
      'UCS_TECHNICAL_USER_PASSWORD',
      'No TechnicalUser password provided for the UCS System system',
    );

    return {
      url: url,
      technicalUserName: technicalUserName,
      technicalUserPassword: technicalUserPassword,
    };
  }

  private getRequiredConfigParameter(
    environment_parameter_key: string,
    error_message = 'Missing environment parameter',
  ): string {
    const parameter = this.nestConfigurationService.get<string>(
      environment_parameter_key,
    );
    const isParameterSpecified = parameter !== undefined;
    if (!isParameterSpecified) {
      throw new Error(
        `${error_message}. Have you specified the ${environment_parameter_key} parameter.`,
      );
    }
    return parameter!;
  }
}
