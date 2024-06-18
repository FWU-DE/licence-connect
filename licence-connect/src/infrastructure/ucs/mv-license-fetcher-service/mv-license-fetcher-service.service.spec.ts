import { Test, TestingModule } from '@nestjs/testing';
import { MVLicenceFetcherService } from './mv-license-fetcher-service.service';
import { HttpService } from '@nestjs/axios';
import { of } from 'rxjs';

describe('MvLicenseFetcherService', () => {
  let service: MVLicenceFetcherService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        MVLicenceFetcherService,
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

    service = module.get<MVLicenceFetcherService>(MVLicenceFetcherService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
