import { Module } from '@nestjs/common';
import { InMemoryRepositoryService } from './repository/in-memory-repository.service';

@Module({
  providers: [InMemoryRepositoryService],
  exports: [InMemoryRepositoryService],
})
export class LicencesModule {}
