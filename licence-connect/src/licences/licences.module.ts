import { Module } from '@nestjs/common';
import { LicencesController } from './licences.controller';
import { MVLicenceService } from './mv/mv-licence.service';
import { HttpModule } from '@nestjs/axios';

@Module({
  imports: [HttpModule],
  controllers: [LicencesController],
  providers: [MVLicenceService],
})
export class LicencesModule {}
