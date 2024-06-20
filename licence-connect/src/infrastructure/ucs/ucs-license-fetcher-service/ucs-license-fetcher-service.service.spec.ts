import { Test, TestingModule } from '@nestjs/testing';
import { UCSLicenseFetcherService as UCSLicenseFetcherService } from './ucs-license-fetcher-service.service';
import { HttpService } from '@nestjs/axios';
import { of } from 'rxjs';

describe('UCSLicenseFetcherService', () => {
  let service: UCSLicenseFetcherService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UCSLicenseFetcherService,
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

    service = module.get<UCSLicenseFetcherService>(UCSLicenseFetcherService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
