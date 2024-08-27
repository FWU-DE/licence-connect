import { LicenceManagementConfiguration } from '@licence-management/domain/licence-management-configuration';
import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { UCSLicenceProviderConfig } from './ucs-licence-provider-config';

@Injectable()
export class UcsConfigurationService {
  constructor(private readonly nestConfigurationService: ConfigService) {}
  public getConfiguration(): LicenceManagementConfiguration {
    return {
      licenceManagementApiKey: this.nestConfigurationService.get<string>(
        'LICENCE_MANAGER_API_KEY',
      ),
    };
  }

  public getApiKey(): string | undefined {
    return this.nestConfigurationService.get<string>('UCS_API_KEY');
  }

  public getUcsConfiguration(): UCSLicenceProviderConfig {
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
