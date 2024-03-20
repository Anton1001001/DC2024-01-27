﻿using DC.Exceptions;
using FluentValidation;

namespace DC.Middleware
{
	public class GlobalErrorHandlerMiddleware(RequestDelegate next)
	{
		private readonly RequestDelegate _next = next;

		public async Task InvokeAsync(HttpContext context)
		{
			try
			{
				await _next(context);
			}
			catch (Exception ex)
			{
				context.Response.StatusCode = ex switch
				{
					NotFoundException => StatusCodes.Status404NotFound,
					ValidationException => StatusCodes.Status400BadRequest,
					_ => StatusCodes.Status500InternalServerError
				};
				await context.Response.WriteAsJsonAsync(new { ErrorMessage =  ex.Message });
			}
		}
	}

	public static class GlobalErrorHandlerMiddlewareExtension
	{
		public static IApplicationBuilder UseGlobalErrorHandler(this IApplicationBuilder app)
		{
			return app.UseMiddleware<GlobalErrorHandlerMiddleware>();
		}
	}
}
