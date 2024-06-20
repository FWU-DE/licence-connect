import { Test, TestingModule } from '@nestjs/testing';
import { UCSLicenceFetcherService as UCSLicenceFetcherService } from './ucs-license-fetcher-service.service';
import { HttpService } from '@nestjs/axios';
import { of } from 'rxjs';

describe('UCSLicenseFetcherService', () => {
  let service: UCSLicenceFetcherService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UCSLicenceFetcherService,
        {
          provide: HttpService,
          useValue: {
            get: jest.fn(() =>
              of({
                // your response body goes here
              }),
            ),
          },
        },
      ],
    }).compile();

    service = module.get<UCSLicenceFetcherService>(UCSLicenceFetcherService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
