import { Module } from '@nestjs/common';
import { LicencesModule } from './licences/licences.module';

@Module({
  imports: [LicencesModule],
  controllers: [],
  providers: [],
})
export class AppModule {}
