import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { AuthenticationModule } from '@cross-cutting-concerns/authentication/infrastructure/authentication.module';
import { VidisController } from './vidis.controller';
import { ConfigModule } from '@nestjs/config';
import { VidisConfigurationService } from './configuration/vidis-configuration.service';
import { LicencesModule } from '@licences/infrastructure/licences.module';

@Module({
  imports: [HttpModule, AuthenticationModule, ConfigModule, LicencesModule],
  controllers: [VidisController],
  providers: [VidisConfigurationService],
  exports: [VidisConfigurationService, ConfigModule],
})
export class VidisModule {}
