package com.example.apigateway.filter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class AcessFilter extends ZuulFilter {
	private static Logger log = LoggerFactory.getLogger(AcessFilter.class);

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();  
		HttpServletRequest request = ctx.getRequest();  

		log.info("send {} request to {}",request.getMethod(), request.getRequestURL().toString());
		
		String acessToken = request.getParameter("acessToken");
		System.out.println("执行AcessFilter .....AcessFilter=" + acessToken 	);

		//如果用户名和密码都正确，则继续执行下一个filter
		if("abc".equals(acessToken) ){
			ctx.setSendZuulResponse(true);//会进行路由，也就是会调用api服务提供者
			ctx.setResponseStatusCode(200);
			ctx.set("isOK",true);//可以把一些值放到ctx中，便于后面的filter获取使用
		}else{
			ctx.setSendZuulResponse(false);//不需要进行路由，也就是不会调用api服务提供者
			ctx.setResponseStatusCode(401);
			ctx.set("isOK",false);//可以把一些值放到ctx中，便于后面的filter获取使用
			//返回内容给客户端
			ctx.setResponseBody("{\"result\":\"acessToken not correct!\"}");// 返回错误内容  
		}

		return null;
	}



	/**
	 * 判断该过滤器是否要执行。我们可以通过此方法来指定过滤器的有效范围
	 */
	@Override
	public boolean shouldFilter() {
		return true;
	}

	/**
	 * 通过int值来定义过滤器的执行顺序，数值越小优先级越高
	 */
	@Override
	public int filterOrder() {
		return 0;
	}

	/**
	 * 过滤器的类型 pre：可以在请求被路由之前调用。routing：在路由请求时候被调用。post：在routing和error过滤器之后被调用。error：处理请求时发生错误时被调用。
	 */
	@Override
	public String filterType() {
		return "pre";
	}

}
