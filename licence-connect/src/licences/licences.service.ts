import { Injectable } from '@nestjs/common';
import { LCLicences } from './types';
import { HttpService } from '@nestjs/axios';

@Injectable()
export class LicenceService {
  constructor(private readonly httpService: HttpService) {}

  public getLicences(_id: string): LCLicences {
    return { hasLicence: false, licences: [] };
  }

  private getAuthentificationToken(): string {
    return '';
  }
}
