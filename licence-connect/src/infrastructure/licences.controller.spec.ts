import { Test, TestingModule } from '@nestjs/testing';
import { LicencesController } from './licences.controller';
import { HttpService } from '@nestjs/axios';
import { ucsResponseWithLicences } from '../domain/ucs/example-data';
import { AxiosResponse } from 'axios';
import { UCSStudent } from 'domain/ucs/ucs-types';
import { ApiKeyGuard } from './authentication/api-key.guard';
import { ApiKeyService } from './authentication/api-key.service';
import { InMemoryRepositoryService } from './licences/repository/in-memory-repository.service';

describe('LicencesController', () => {
  let licencesController: LicencesController;

  const buildSuccessfullResponse: (ucsStudent: UCSStudent) => AxiosResponse = (
    student: UCSStudent,
  ) => {
    return {
      data: { 'http://www.bildungslogin.de/licenses': student },
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
        InMemoryRepositoryService,
        ApiKeyGuard,
        ApiKeyService,
        {
          provide: HttpService,
          useValue: {
            get: jest.fn((_url: string) =>
              buildSuccessfullResponse(
                ucsResponseWithLicences['http://www.bildungslogin.de/licenses'],
              ),
            ),
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
