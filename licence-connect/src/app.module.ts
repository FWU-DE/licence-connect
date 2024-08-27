import { Module } from '@nestjs/common';
import { VidisModule } from './vidis/infrastructure/vidis.module';
import { LicenceManagementModule } from './licence-management/infrastructure/licence-management.module';
import { VidisConfigurationService } from './vidis/infrastructure/configuration/vidis-configuration.service';
import { LicenceManagementConfigurationService } from './licence-management/infrastructure/configuration/licence-management-configuration.service';
import { UcsModule } from './ucs/infrastructure/ucs.module';

@Module({
  imports: [VidisModule, LicenceManagementModule, UcsModule],
  controllers: [],
  providers: [VidisConfigurationService, LicenceManagementConfigurationService],
})
export class AppModule {}
