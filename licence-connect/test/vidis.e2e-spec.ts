import { Test, TestingModule } from '@nestjs/testing';
import * as request from 'supertest';
import { INestApplication } from '@nestjs/common';
import { AppModule } from '../src/app.module';
import { RequestFromVidisCore } from '@vidis/domain/request-from-vidis-core';
import { TEST_ENV_VARIABLES } from './test-env';

describe('Vidis (e2e)', () => {
  let app: INestApplication;

  const vidisApiKey = TEST_ENV_VARIABLES.VIDIS_API_KEY;

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

  describe('API KEY v1/licence (POST)', () => {
    describe('With valid api key', () => {
      it('in header', () => {
        return request(app.getHttpServer())
          .post('/licences/request')
          .set({ 'X-API-KEY': vidisApiKey })
          .send(requestWithNonExistingUser)
          .expect(200)
          .expect(`{"hasLicence":false,"licences":[]}`);
      });
    });

    describe('With invalid api key', () => {
      it('in header', () => {
        return request(app.getHttpServer())
          .post('/licences/request')
          .set({ 'X-API-KEY': `${vidisApiKey}wrongApiKey` })
          .send(requestWithNonExistingUser)
          .expect(403);
      });

      it('in query', () => {
        return request(app.getHttpServer())
          .post(`/licences/request?X-API-KEY=${vidisApiKey}wrongApiKey`)
          .send(requestWithNonExistingUser)
          .expect(403);
      });

      it('in query (valid but in query)', () => {
        return request(app.getHttpServer())
          .post(`/licences/request?X-API-KEY=${vidisApiKey}`)
          .send(requestWithNonExistingUser)
          .expect(403);
      });
    });

    describe('Without api key', () => {
      it('in query', () => {
        return request(app.getHttpServer())
          .post('/licences/request')
          .send(requestWithNonExistingUser)
          .expect(403);
      });
    });
  });
});
