import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { AuthenticationModule } from '@cross-cutting-concerns/authentication/infrastructure/authentication.module';
import { LicenceManagementController } from './licence-management.controller';
import { LicenceManagementConfigurationService } from './configuration/licence-management-configuration.service';
import { ConfigModule } from '@nestjs/config';
import { LicencesModule } from '@licences/infrastructure/licences.module';

@Module({
  imports: [
    HttpModule,
    AuthenticationModule,
    ConfigModule.forRoot(),
    LicencesModule,
  ],
  controllers: [LicenceManagementController],
  providers: [LicenceManagementConfigurationService],
  exports: [LicenceManagementConfigurationService, ConfigModule],
})
export class LicenceManagementModule {}
