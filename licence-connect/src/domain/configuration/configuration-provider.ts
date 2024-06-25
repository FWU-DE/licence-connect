import { EnvironmentConfiguration } from 'domain/environment-configuration';

export interface ConfigurationProvider {
  getConfiguration(): EnvironmentConfiguration;
}
