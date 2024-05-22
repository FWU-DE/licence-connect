import { Module } from '@nestjs/common';
import { LicencesController } from './licences.controller';
import { LicenceService } from './licences.service';

@Module({
  controllers: [LicencesController],
  providers: [LicenceService],
})
export class LicencesModule {}
