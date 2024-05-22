import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { LicencesModule } from './licences/licences.module';

@Module({
  imports: [LicencesModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
