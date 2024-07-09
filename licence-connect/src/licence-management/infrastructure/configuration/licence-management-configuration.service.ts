import { LicenceManagementConfiguration } from '@licence-management/domain/licence-management-configuration';
import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

@Injectable()
export class LicenceManagementConfigurationService {
  constructor(private readonly nestConfigurationService: ConfigService) {}
  public getConfiguration(): LicenceManagementConfiguration {
    return {
      licenceManagementApiKey: this.nestConfigurationService.get<string>(
        'LICENCE_MANAGER_API_KEY',
      ),
    };
  }
}
