import { Module } from '@nestjs/common';
import { LicencesController } from './licences.controller';
import { LicenceService } from './licences.service';
import { HttpModule } from '@nestjs/axios';

@Module({
  imports: [HttpModule],
  controllers: [LicencesController],
  providers: [LicenceService],
})
export class LicencesModule {}
