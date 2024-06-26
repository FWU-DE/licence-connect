import { Module } from '@nestjs/common';
import { VidisModule } from './vidis/infrastructure/vidis.module';
import { LicenceManagementModule } from './licence-management/infrastructure/licence-management.module';
import { LicencesModule } from './licences/infrastructure/licences.module';
import { VidisConfigurationService } from './vidis/infrastructure/configuration/vidis-configuration.service';
import { LicenceManagementConfigurationService } from './licence-management/infrastructure/configuration/licence-management-configuration.service';

@Module({
  imports: [VidisModule, LicenceManagementModule, LicencesModule],
  controllers: [],
  providers: [VidisConfigurationService, LicenceManagementConfigurationService],
})
export class AppModule {}
