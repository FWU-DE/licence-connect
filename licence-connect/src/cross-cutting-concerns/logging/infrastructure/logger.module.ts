import { Module } from '@nestjs/common';
import { WinstonLoggingService } from './winston-logging.service';
import {
  utilities as nestWinstonModuleUtilities,
  WinstonModule,
} from 'nest-winston';
import * as winston from 'winston';
import { LoggerConfigurationService } from './logger-configuration.service';
import { ConfigModule, ConfigService } from '@nestjs/config';

export const LOGGER_TOKEN = 'LCLogger';

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
  exports: [LOGGER_TOKEN, LoggerConfigurationService],
  imports: [
    ConfigModule.forRoot(),
    WinstonModule.forRootAsync({
      useFactory: (configService: LoggerConfigurationService) => ({
        format: defaultFormat(),
        level: configService.getLoggingConfiguration().loggingPath,
        transports: [
          new winston.transports.Console({}),
          new winston.transports.File({
            filename: `${configService.getLoggingConfiguration().loggingPath}/${configService.getLoggingConfiguration().loggingPath}`,
          }),
        ],
      }),
      inject: [LoggerConfigurationService],
      imports: [LoggerModule],
    }),
  ],
  providers: [
    {
      provide: LOGGER_TOKEN,
      useClass: WinstonLoggingService,
    },
    LoggerConfigurationService,
  ],
})
export class LoggerModule {}
