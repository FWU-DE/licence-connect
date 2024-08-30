import { Module } from '@nestjs/common';
import { UcsController } from './ucs.controller';
import { UcsRepositoryService } from './repository/ucs-repository.service';
import { UCSLicenseFetcherService } from './ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { HttpModule } from '@nestjs/axios';
import { UcsConfigurationService } from './configuration/ucs-configuration.service';
import { ConfigModule } from '@nestjs/config';
import { LoggerModule } from '@cross-cutting-concerns/logging/infrastructure/logger.module';

@Module({
  imports: [HttpModule, ConfigModule.forRoot(), LoggerModule],
  controllers: [UcsController],
  providers: [
    UcsRepositoryService,
    UCSLicenseFetcherService,
    UcsConfigurationService,
  ],
})
export class UcsModule {}
