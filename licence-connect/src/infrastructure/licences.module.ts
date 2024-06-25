import { Module } from '@nestjs/common';
import { LicencesController } from './licences.controller';
import { HttpModule } from '@nestjs/axios';
import { AuthenticationModule } from '../infrastructure/authentication/authentication.module';
import { UcsRepositoryService } from './ucs/repository/ucs-repository.service';
import { UCSLicenseFetcherService } from './ucs/ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { InMemoryRepositoryService } from './licences/repository/in-memory-repository.service';
import { ConfigurationModule } from './configuration/configuration.module';

@Module({
  imports: [HttpModule, AuthenticationModule, ConfigurationModule],
  controllers: [LicencesController],
  providers: [
    UcsRepositoryService,
    UCSLicenseFetcherService,
    InMemoryRepositoryService,
  ],
  exports: [
    UcsRepositoryService,
    UCSLicenseFetcherService,
    InMemoryRepositoryService,
  ],
})
export class LicencesModule {}
