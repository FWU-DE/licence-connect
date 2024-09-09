import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';
import { AppModule } from '../src/app.module';
import { TEST_ENV_VARIABLES } from './test-env';
import { UcsLicenseFetcherService } from '@ucs/infrastructure/ucs-license-fetcher-service/ucs-license-fetcher-service.service';
import { createUcsStudentData } from './ucs-student-data';
import { UcsRequestFromVidisCore } from '@ucs/domain/ucs-request-from-vidis-core';

const ucsLicenceKey = TEST_ENV_VARIABLES.VIDIS_API_KEY;

describe('UCS (e2e)', () => {
  let app: INestApplication;

  process.env = { ...process.env, ...TEST_ENV_VARIABLES };

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    })
      .overrideProvider(UcsLicenseFetcherService)
      .useValue({
        fetchUcsStudentFromId: jest.fn((studentId: string) =>
          Promise.resolve(createUcsStudentData(studentId)),
        ),
      })
      .compile();

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  const requestFromVidis: UcsRequestFromVidisCore = {
    userId: 'student.42',
    clientId: 'sodix-editor-o',
    federalState: 'DE-MV',
  };

  describe('API KEY v1/ucs/request', () => {
    it('Test', () => {
      return request(app.getHttpServer())
        .post(`/ucs/request`)
        .set({ 'X-API-KEY': ucsLicenceKey })
        .send(requestFromVidis)
        .expect(200)
        .expect(createUcsStudentData('student.42'));
    });
  });
});
