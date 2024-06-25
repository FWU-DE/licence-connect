import { Injectable } from '@nestjs/common';
import { ConfigurationService } from 'infrastructure/configuration/configuration.service';

@Injectable()
export class ApiKeyService {
  constructor(private readonly configurationService: ConfigurationService) {}

  public isApiKeyValid(apiKey: string): boolean {
    return apiKey === this.configurationService.getConfiguration().vidisApiKey;
  }
}
