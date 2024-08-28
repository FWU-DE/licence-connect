import { Injectable } from '@nestjs/common';
import { ApiKeyGuard } from '../../../cross-cutting-concerns/authentication/infrastructure/api-key.guard';
import { UcsConfigurationService } from '../configuration/ucs-configuration.service';

@Injectable()
export class UcsApiKeyGuard extends ApiKeyGuard {
  constructor(private readonly configurationService: UcsConfigurationService) {
    super();
  }

  getApiKey(): string | undefined {
    return this.configurationService.getApiKey();
  }
}
