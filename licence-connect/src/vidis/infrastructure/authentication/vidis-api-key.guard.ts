import { Injectable } from '@nestjs/common';
import { ApiKeyGuard } from '../../../cross-cutting-concerns/authentication/infrastructure/api-key.guard';
import { VidisConfigurationService } from '@vidis/infrastructure/configuration/vidis-configuration.service';

@Injectable()
export class VidisApiKeyGuard extends ApiKeyGuard {
  constructor(
    private readonly configurationService: VidisConfigurationService,
  ) {
    super();
  }

  getApiKey(): string {
    return this.configurationService.getConfiguration().vidisApiKey;
  }
}
