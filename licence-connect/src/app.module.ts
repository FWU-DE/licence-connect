import { Module } from '@nestjs/common';
import { LicencesModule } from './infrastructure/licences.module';
import { UcsRepositoryService } from './infrastructure/ucs/repository/ucs-repository.service';
import { ConfigurationModule } from './configuration/configuration.module';
import { UcsRepositoryService } from './infrastructure/ucs/repository/ucs-repository.service';

@Module({
  imports: [LicencesModule, ConfigurationModule],
  controllers: [],
  providers: [UcsRepositoryService],
})
export class AppModule {}
