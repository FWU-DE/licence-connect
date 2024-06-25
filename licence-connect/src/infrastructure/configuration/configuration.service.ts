import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { ConfigurationProvider } from 'domain/configuration/configuration-provider';
import { EnvironmentConfiguration } from 'domain/environment-configuration';

@Injectable()
export class ConfigurationService implements ConfigurationProvider {
  constructor(private readonly nestConfigurationService: ConfigService) {}
  public getConfiguration(): EnvironmentConfiguration {
    return {
      vidisApiKey: this.nestConfigurationService.get<string>('VIDIS_API_KEY'),
    };
  }
}
