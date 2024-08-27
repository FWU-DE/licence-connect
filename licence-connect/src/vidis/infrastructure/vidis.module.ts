import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { AuthenticationModule } from '@cross-cutting-concerns/authentication/infrastructure/authentication.module';
import { ConfigModule } from '@nestjs/config';
import { VidisConfigurationService } from './configuration/vidis-configuration.service';
import { DefaultLicenceStrategyFactory } from './default-licence-strategy-factory.service';
import { VidisController } from './vidis.controller';
import { LicenceManagementModule } from '@licence-management/infrastructure/licence-management.module';

@Module({
  imports: [
    HttpModule,
    AuthenticationModule,
    ConfigModule.forRoot(),
    LicenceManagementModule,
  ],
  controllers: [VidisController],
  providers: [
    VidisConfigurationService,
    DefaultLicenceStrategyFactory,
    {
      provide: 'LicenceStrategyFactory',
      useClass: DefaultLicenceStrategyFactory,
    },
  ],
  exports: [VidisConfigurationService, ConfigModule],
})
export class VidisModule {}
