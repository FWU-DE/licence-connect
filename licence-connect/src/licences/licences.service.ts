import { Injectable } from '@nestjs/common';
import { Licences } from './types';
import { HttpService } from '@nestjs/axios';

@Injectable()
export class LicenceService {
  constructor(private readonly httpService: HttpService) {}

  public getLicences(_id: string): Licences {
    return { hasLicence: false, licences: [] };
  }

  private getAuthentificationToken(): string {
    return '';
  }
}
