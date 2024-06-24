import { Module } from '@nestjs/common';
import { LicencesModule } from './infrastructure/licences.module';
import { ConfigurationModule } from './configuration/configuration.module';

@Module({
  imports: [LicencesModule, ConfigurationModule],
  controllers: [],
  providers: [],
})
export class AppModule {}
