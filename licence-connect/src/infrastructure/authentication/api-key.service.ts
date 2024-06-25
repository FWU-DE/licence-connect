import { Injectable } from '@nestjs/common';

@Injectable()
export class ApiKeyService {
  constructor() {}

  public isApiKeyValid(apiKey: string): boolean {
    return apiKey === 'test';
  }
}
