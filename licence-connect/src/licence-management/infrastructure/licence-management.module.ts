import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { AuthenticationModule } from '@cross-cutting-concerns/authentication/infrastructure/authentication.module';
import { LicenceManagementController } from './licence-management.controller';
import { LicenceManagementConfigurationService } from './configuration/licence-management-configuration.service';
import { ConfigModule } from '@nestjs/config';
import { AddLicenceForStudentUseCaseFactoryService } from './usecase-factories/add-licence-for-student-use-case-factory.service';
import { RemoveLicenceForStudentUseCaseFactoryService } from './usecase-factories/remove-licence-for-student-use-case-factory.service';
import { LicencesModule } from '@licences/infrastructure/licences.module';

@Module({
  imports: [HttpModule, AuthenticationModule, ConfigModule, LicencesModule],
  controllers: [LicenceManagementController],
  providers: [
    LicenceManagementConfigurationService,
    AddLicenceForStudentUseCaseFactoryService,
    RemoveLicenceForStudentUseCaseFactoryService,
  ],
  exports: [
    LicenceManagementConfigurationService,
    AddLicenceForStudentUseCaseFactoryService,
    RemoveLicenceForStudentUseCaseFactoryService,
    ConfigModule,
  ],
})
export class LicenceManagementModule {}
