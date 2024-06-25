import { Module } from '@nestjs/common';
import { ConfigurationModule } from '../configuration/configuration.module';

@Module({
  exports: [ConfigurationModule],
  imports: [ConfigurationModule],
  controllers: [],
  providers: [],
})
export class AuthenticationModule {}
