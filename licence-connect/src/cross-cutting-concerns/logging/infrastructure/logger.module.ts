import { Module } from '@nestjs/common';
import { WinstonLoggingService } from './winston-logging.service';
import {
  utilities as nestWinstonModuleUtilities,
  WinstonModule,
} from 'nest-winston';
import * as winston from 'winston';
import { LoggingLevel, loggingLevel } from '../domain/logger';

export const LOGGER_TOKEN = 'LCLogger';

const validateLogLevel = (level?: string) =>
  level !== undefined && level in loggingLevel
    ? (level as LoggingLevel)
    : undefined;

const defaultFormat = (featureName: string = 'LicenceConnect') =>
  winston.format.combine(
    winston.format.timestamp(),
    winston.format.ms(),
    nestWinstonModuleUtilities.format.nestLike(featureName, {
      colors: true,
      prettyPrint: true,
      processId: true,
      appName: true,
    }),
  );

@Module({
  exports: [LOGGER_TOKEN],
  imports: [
    WinstonModule.forRoot({
      format: defaultFormat(),
      level: validateLogLevel(process.env.LOG_LEVEL) ?? 'info',
      transports: [
        new winston.transports.Console({}),
        new winston.transports.File({
          filename: `${process.env.LOG_PATH ?? 'log'}/${process.env.LOG_FILE ?? 'licence-connect.log'}`,
        }),
      ],
    }),
  ],
  controllers: [],
  providers: [
    {
      provide: LOGGER_TOKEN,
      useClass: WinstonLoggingService,
    },
  ],
})
export class LoggerModule {}
