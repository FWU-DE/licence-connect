import { Module } from '@nestjs/common';
import { LicencesController } from './licences.controller';
import { HttpModule } from '@nestjs/axios';
import { AuthenticationModule } from '../infrastructure/authentication/authentication.module';
import { UcsRepositoryService } from './ucs/repository/ucs-repository.service';
import { UCSLicenseFetcherService } from './ucs/ucs-license-fetcher-service/ucs-license-fetcher-service.service';

@Module({
  imports: [HttpModule, AuthenticationModule],
  controllers: [LicencesController],
  providers: [UcsRepositoryService, UCSLicenseFetcherService],
  exports: [UcsRepositoryService],
})
export class LicencesModule {}
