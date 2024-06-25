import { Injectable } from '@nestjs/common';
import { ApiKeyGuard } from '../api-key.guard';
import { ConfigurationService } from '../../configuration/configuration.service';

@Injectable()
export class VidisApiKeyGuard extends ApiKeyGuard {
  constructor(private readonly configurationService: ConfigurationService) {
    super();
  }

  getApiKey(): string {
    return this.configurationService.getConfiguration().vidisApiKey;
  }
}
