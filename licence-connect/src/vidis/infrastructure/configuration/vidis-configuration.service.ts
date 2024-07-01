import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { VidisConfiguration } from '@vidis/domain/vidis-configuration';

@Injectable()
export class VidisConfigurationService {
  constructor(private readonly nestConfigurationService: ConfigService) {}
  public getConfiguration(): VidisConfiguration {
    return {
      vidisApiKey: this.nestConfigurationService.get<string>('VIDIS_API_KEY'),
    };
  }
}
