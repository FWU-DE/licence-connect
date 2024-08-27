import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';
import { AppModule } from '../src/app.module';
import { RequestFromVidisCore } from '@vidis/domain/request-from-vidis-core';
import { TEST_ENV_VARIABLES } from './test-env';

describe('LicenceManagementController (e2e)', () => {
  let app: INestApplication;

  const licenceManagerApiKey = TEST_ENV_VARIABLES.LICENCE_MANAGER_API_KEY;

  process.env = { ...process.env, ...TEST_ENV_VARIABLES };

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    await app.init();
  });

  const requestWithNonExistingUser: RequestFromVidisCore = {
    userId: '00000',
    clientId: 'sodix-editor-o',
    schulkennung: 'DE-RP-SN-51201',
    bundesland: 'DE-MV',
  };

  describe('v1/licence/ (POST)', () => {
    it('Add licences', () => {
      const licenceRequest = requestWithNonExistingUser;
      licenceRequest.userId = 'Student1';

      return request(app.getHttpServer())
        .post('/licences/add')
        .set({ 'X-API-KEY': licenceManagerApiKey })
        .send({
          studentId: 'Student1',
          licencesToAdd: [{ license_code: '1111' }],
        })
        .expect(201);
    });

    it('Reject licence request without licence_code', () => {
      const licenceRequest = requestWithNonExistingUser;
      licenceRequest.userId = 'Student1';

      return request(app.getHttpServer())
        .post('/licences/add')
        .set({ 'X-API-KEY': licenceManagerApiKey })
        .send({
          studentId: 'Student1',
          licencesToAdd: ['1111'],
        })
        .expect(400);
    });

    it('Reject licence request without studentId', () => {
      const licenceRequest = requestWithNonExistingUser;
      licenceRequest.userId = 'Student1';

      return request(app.getHttpServer())
        .post('/licences/add')
        .set({ 'X-API-KEY': licenceManagerApiKey })
        .send({
          licencesToAdd: [{ license_code: '1111' }],
        })
        .expect(400);
    });
  });
});
