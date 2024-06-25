import { Module } from '@nestjs/common';
import { ApiKeyService } from './api-key.service';
import { ConfigurationModule } from 'infrastructure/configuration/configuration.module';

@Module({
  exports: [ApiKeyService],
  imports: [ConfigurationModule],
  controllers: [],
  providers: [ApiKeyService],
})
export class AuthenticationModule {}
