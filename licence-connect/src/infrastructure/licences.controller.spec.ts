import { Test, TestingModule } from '@nestjs/testing';
import { LicencesController } from './licences.controller';
import { HttpService } from '@nestjs/axios';
import {
  ucsResponseWithLicences,
  incomingVidisCoreRequest,
} from './ucs/test/example_data';
import { AxiosResponse } from 'axios';
import { ResponseFromUCS } from './ucs/UCSTypes';
import { MVLicenceFetcherService } from './ucs/mv-license-fetcher-service/mv-license-fetcher-service.service';
import { MVLicenceService } from './ucs/mv-license-service/mv-license-service.service';

describe('LicencesController', () => {
  let licencesController: LicencesController;

  const buildSuccessfullResponse: (MVStudent) => AxiosResponse = (
    student: ResponseFromUCS,
  ) => {
    return {
      data: student,
      status: 200,
      statusText: 'OK',
      headers: undefined,
      config: undefined,
    };
  };

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      controllers: [LicencesController],
      providers: [
        MVLicenceService,
        MVLicenceFetcherService,
        {
          provide: HttpService,
          useValue: {
            get: jest.fn((url: string) => {
              if (
                url ===
                `/ucsschool/apis/bildungslogin/v1/user/${incomingVidisCoreRequest.sub}`
              ) {
                return buildSuccessfullResponse(ucsResponseWithLicences);
              }
            }),
          },
        },
      ],
    }).compile();

    licencesController = app.get<LicencesController>(LicencesController);
  });

  it('should be created"', () => {
    expect(licencesController).toBeTruthy;
  });
});
