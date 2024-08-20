import { LicenceStrategy } from './licence-strategy';

export interface LicenceStrategyFactory {
  determineLicenceStrategy(): LicenceStrategy;
}
