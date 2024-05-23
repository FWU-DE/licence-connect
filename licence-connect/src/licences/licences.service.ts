import { Injectable } from '@nestjs/common';
import { Licences } from './types';
import { HttpService } from '@nestjs/axios';

@Injectable()
export class LicenceService {
  constructor(private readonly httpService: HttpService) {}

  getLicences(): Licences {
    return { hasLicence: false, licences: [] };
  }
}
