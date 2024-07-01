import { Injectable } from '@nestjs/common';
import { LicenceManagementConfigurationService } from '@licence-management/infrastructure/configuration/licence-management-configuration.service';
import { ApiKeyGuard } from '@cross-cutting-concerns/authentication/infrastructure/api-key.guard';

@Injectable()
export class LicenceManagementApiKeyGuard extends ApiKeyGuard {
  constructor(
    private readonly configurationService: LicenceManagementConfigurationService,
  ) {
    super();
  }

  override getApiKey(): string {
    return this.configurationService.getConfiguration().licenceManagementApiKey;
  }
}
