import { InMemoryRepositoryService } from '@licence-management/infrastructure/repository/in-memory-repository.service';
import { Injectable } from '@nestjs/common';
import { LicenceStrategy } from '@vidis/domain/licence-strategy';
import { LicenceStrategyFactory } from '@vidis/domain/licence-strategy-factory';

@Injectable()
export class DefaultLicenceStrategyFactory implements LicenceStrategyFactory {
  constructor(private readonly inMemoryStrategy: InMemoryRepositoryService) {}

  determineLicenceStrategy(): LicenceStrategy {
    return this.inMemoryStrategy;
  }
}
