import { Module } from '@nestjs/common';
import { LicencesController } from './licences.controller';
import { HttpModule } from '@nestjs/axios';
import { UCSLicenseFetcherService } from './ucs/ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { AuthenticationModule } from '../infrastructure/authentication/authentication.module';

@Module({
  imports: [HttpModule, AuthenticationModule],
  controllers: [LicencesController],
  providers: [UCSLicenseFetcherService],
})
export class LicencesModule {}
