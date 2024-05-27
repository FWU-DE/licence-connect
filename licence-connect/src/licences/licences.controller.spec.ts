import { Test, TestingModule } from '@nestjs/testing';
import { LicencesController } from './licences.controller';
import { LicenceService } from './licences.service';
import { HttpService } from '@nestjs/axios';
import { incomingVidisCoreRequest } from './test_data';
import { AxiosResponse } from 'axios';
import { Licence } from './types';

describe('LicencesController', () => {
  let licencesController: LicencesController;

  const licences: Licence[] = ['0', '1', '2', '3'];

  type MVStudent = {
    id: string;
    first_name: string;
    last_name: string;
    licences: [Licence];
    context: {
      licences: [Licence];
      classes: [{ licences: [Licence] }];
      workgroups: [{ licences: [Licence] }];
    };
  };

  const studentWithLicences: MVStudent = {
    id: '421c4a1a-c140-420c-aed1-622832211a11',
    first_name: 'Maya',
    last_name: 'WithLicence',
    licences: [licences[0]],
    context: {
      licences: [licences[1]],
      classes: [{ licences: [licences[2]] }],
      workgroups: [{ licences: [licences[3]] }],
    },
  };

  const _studentWithoutLicences = {
    id: '421c4a1a-c140-420c-aed1-622832211a12',
    first_name: 'Maya',
    last_name: 'WithoutLicence',
    licences: [],
    context: {
      licences: [],
      classes: [],
      workgroups: [],
    },
  };

  const buildSuccessfullResponse: (MVStudent) => AxiosResponse = (
    student: MVStudent,
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
        LicenceService,
        {
          provide: HttpService,
          useValue: {
            get: jest.fn((url: string) => {
              if (
                url ===
                `/ucsschool/apis/bildungslogin/v1/user/${incomingVidisCoreRequest.sub}`
              ) {
                return buildSuccessfullResponse(studentWithLicences);
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

  it('should answer with with no licences"', () => {
    expect(
      licencesController.getLicences({ body: incomingVidisCoreRequest }),
    ).toStrictEqual({
      hasLicence: false,
      licences: [],
    });
  });
});
