import { Module } from '@nestjs/common';
import { ApiKeyService } from './api-key.service';

@Module({
  exports: [ApiKeyService],
  imports: [],
  controllers: [],
  providers: [ApiKeyService],
})
export class AuthenticationModule {}
