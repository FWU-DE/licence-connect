import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { AuthenticationModule } from '@cross-cutting-concerns/authentication/infrastructure/authentication.module';
import { LicenceManagementController } from './licence-management.controller';
import { LicenceManagementConfigurationService } from './configuration/licence-management-configuration.service';
import { ConfigModule } from '@nestjs/config';
import { InMemoryRepositoryService } from './repository/in-memory-repository.service';

@Module({
  imports: [HttpModule, AuthenticationModule, ConfigModule.forRoot()],
  controllers: [LicenceManagementController],
  providers: [
    LicenceManagementConfigurationService,
    InMemoryRepositoryService,
    {
      provide: 'LicenceSource',
      useClass: InMemoryRepositoryService,
    },
  ],
  exports: [
    LicenceManagementConfigurationService,
    ConfigModule,
    InMemoryRepositoryService,
  ],
})
export class LicenceManagementModule {}
