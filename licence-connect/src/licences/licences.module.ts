import { Module } from '@nestjs/common';
import { LicencesController } from './licences.controller';

@Module({
  controllers: [LicencesController],
})
export class LicencesModule {}
