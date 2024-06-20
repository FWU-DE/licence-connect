import { Injectable } from '@nestjs/common';

@Injectable()
export class ApiKeyService {
  constructor() {}

  public isApiKeyValid(_apiKey: string): boolean {
    return true;
  }
}
