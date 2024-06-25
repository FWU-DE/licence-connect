import { Injectable } from '@nestjs/common';
import { ConfigurationService } from '../../configuration/configuration.service';
import { ApiKeyGuard } from '../api-key.guard';

@Injectable()
export class LicenceManagementApiKeyGuard extends ApiKeyGuard {
  constructor(private readonly configurationService: ConfigurationService) {
    super();
  }

  getApiKey(): string {
    return this.configurationService.getConfiguration().licenceManagerApiKey;
  }
}
