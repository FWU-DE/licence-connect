import { Module } from '@nestjs/common';
import { LicencesModule } from './infrastructure/licences.module';

@Module({
  imports: [LicencesModule],
  controllers: [],
  providers: [],
})
export class AppModule {}
